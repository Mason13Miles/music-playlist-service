package com.amazon.ata.music.playlist.service.dynamodb;

import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AlbumTrackDaoTest {

    @Mock
    private DynamoDBMapper dynamoDbMapper;

    private AlbumTrackDao albumTrackDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        albumTrackDao = new AlbumTrackDao(dynamoDbMapper);
    }

    @Test
    public void getAlbumTrack_validAsinAndTrackNumber_returnsAlbumTrack() {
        // Arrange
        String asin = "album123";
        int trackNumber = 1;

        AlbumTrack expectedTrack = new AlbumTrack();
        expectedTrack.setAsin(asin);
        expectedTrack.setTrackNumber(trackNumber);
        expectedTrack.setAlbumName("Test Album Name");
        expectedTrack.setSongTitle("Test Song Title");

        // Use any() to match the key argument to avoid issues with object references.
        when(dynamoDbMapper.load(eq(AlbumTrack.class), any(AlbumTrack.class))).thenReturn(expectedTrack);

        // Act
        AlbumTrack result = albumTrackDao.getAlbumTrack(asin, trackNumber);

        // Assert
        assertEquals(expectedTrack, result);
        verify(dynamoDbMapper, times(1)).load(eq(AlbumTrack.class), any(AlbumTrack.class));
    }

    @Test
    public void getAlbumTrack_nonExistingAsinAndTrackNumber_returnsNull() {
        // Arrange
        String asin = "album999";
        int trackNumber = 5;

        // Use any() to match the key argument.
        when(dynamoDbMapper.load(eq(AlbumTrack.class), any(AlbumTrack.class))).thenReturn(null);

        // Act
        AlbumTrack result = albumTrackDao.getAlbumTrack(asin, trackNumber);

        // Assert
        assertNull(result);
        verify(dynamoDbMapper, times(1)).load(eq(AlbumTrack.class), any(AlbumTrack.class));
    }
}


