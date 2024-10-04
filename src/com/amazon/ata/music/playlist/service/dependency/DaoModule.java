package com.amazon.ata.music.playlist.service.dependency;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dagger.Provides;
import dagger.Module;

import javax.inject.Inject;
import javax.inject.Singleton;

@Module
public class DaoModule {

    @Singleton
    @Provides
    DynamoDBMapper provideDynamoDBMapper() {
        return new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient(Regions.US_WEST_2));
    }
    private Regions getRegion(){
        return Regions.US_WEST_2;
    }
}
