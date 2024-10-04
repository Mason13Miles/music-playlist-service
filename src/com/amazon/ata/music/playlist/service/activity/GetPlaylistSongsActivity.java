package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazon.ata.music.playlist.service.models.SongOrder;
import com.amazon.ata.music.playlist.service.models.requests.GetPlaylistSongsRequest;
import com.amazon.ata.music.playlist.service.models.results.GetPlaylistSongsResult;
import com.amazon.ata.music.playlist.service.models.SongModel;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the GetPlaylistSongsActivity for the MusicPlaylistService's GetPlaylistSongs API.
 *
 * This API allows the customer to get the list of songs of a saved playlist.
 */
public class GetPlaylistSongsActivity implements RequestHandler<GetPlaylistSongsRequest, GetPlaylistSongsResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;

    /**
     * Instantiates a new GetPlaylistSongsActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     */
    @Inject
    public GetPlaylistSongsActivity(PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
    }

    /**
     * This method handles the incoming request by retrieving the playlist from the database.
     * <p>
     * It then returns the playlist's song list.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     *
     * @param getPlaylistSongsRequest request object containing the playlist ID
     * @return getPlaylistSongsResult result object containing the playlist's list of API defined {@link SongModel}s
     */
    @Override
    public GetPlaylistSongsResult handleRequest(final GetPlaylistSongsRequest getPlaylistSongsRequest, Context context) {
        // Log the received request
        log.info("Received GetPlaylistSongsRequest {}", getPlaylistSongsRequest);

        // Fetch the playlist from the DAO using the playlist ID
        String playlistId = getPlaylistSongsRequest.getId();
        Playlist playlist = playlistDao.getPlaylist(playlistId);
        if (playlist == null) {
            throw new PlaylistNotFoundException("Playlist not found with ID: " + playlistId);
        }

        // Convert AlbumTrack objects to SongModel using ModelConverter
        List<SongModel> songModels = playlist.getSongList().stream()
                .map(ModelConverter::toSongModel)
                .collect(Collectors.toList());

        // Modify the list based on the order parameter
        String order = String.valueOf(getPlaylistSongsRequest.getOrder());
        if (SongOrder.REVERSED.equals(order)) {
            Collections.reverse(songModels);
        } else if (SongOrder.SHUFFLED.equals(order)) {
            Collections.shuffle(songModels);
        } else {
            throw new IllegalArgumentException("invalid Song Order");
        }

        // Return the result with the list of SongModel objects
        return GetPlaylistSongsResult.builder()
                .withSongList(songModels)
                .build();
    }

}
