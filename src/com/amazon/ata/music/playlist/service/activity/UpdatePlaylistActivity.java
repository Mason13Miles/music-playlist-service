package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeChangeException;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.models.requests.UpdatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.UpdatePlaylistResult;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;

import com.amazon.ata.music.playlist.service.util.MusicPlaylistServiceUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * Implementation of the UpdatePlaylistActivity for the MusicPlaylistService's UpdatePlaylist API.
 *
 * This API allows the customer to update their saved playlist's information.
 */
public class UpdatePlaylistActivity implements RequestHandler<UpdatePlaylistRequest, UpdatePlaylistResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;

    /**
     * Instantiates a new UpdatePlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     */
    @Inject
    public UpdatePlaylistActivity(PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
    }

    /**
     * This method handles the incoming request by retrieving the playlist, updating it,
     * and persisting the playlist.
     * <p>
     * It then returns the updated playlist.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     * <p>
     * If the provided playlist name or customer ID has invalid characters, throws an
     * InvalidAttributeValueException
     * <p>
     * If the request tries to update the customer ID,
     * this should throw an InvalidAttributeChangeException
     *
     * @param updatePlaylistRequest request object containing the playlist ID, playlist name, and customer ID
     *                              associated with it
     * @return updatePlaylistResult result object containing the API defined {@link PlaylistModel}
     */
    @Override
    public UpdatePlaylistResult handleRequest(final UpdatePlaylistRequest updatePlaylistRequest, Context context) {
        if (updatePlaylistRequest == null || updatePlaylistRequest.getId() == null) {
            throw new PlaylistNotFoundException("Playlist ID cannot be null.");
        }

        String id = updatePlaylistRequest.getId();
        String newName = updatePlaylistRequest.getName();
        String newCustomerId = updatePlaylistRequest.getCustomerId();

        Playlist playlist = playlistDao.getPlaylist(id);
        if (playlist == null) {
            throw new PlaylistNotFoundException("No playlist found with id: " + id);
        }

        try {
            MusicPlaylistServiceUtils.isValidString(newName);
            MusicPlaylistServiceUtils.isValidString(newCustomerId);
        } catch (Exception e) {
            throw new InvalidAttributeValueException("Invalid characters in playlist name or customer ID.");
        }

        if (!playlist.getCustomerId().equals(newCustomerId)) {
            throw new InvalidAttributeChangeException("Customer ID cannot be modified.");
        }

        playlist.setName(newName);
        playlistDao.savePlaylist(playlist);

        log.info("Updated playlist: {}", playlist);

        PlaylistModel updatedPlaylistModel = PlaylistModel.builder()
                .withId(playlist.getId())
                .withName(playlist.getName())
                .withCustomerId(playlist.getCustomerId())
                .withSongCount(playlist.getSongCount())
                .withTags(playlist.getTags())
                .build();

        return UpdatePlaylistResult.builder()
                .withPlaylist(updatedPlaylistModel)
                .build();
    }

}
