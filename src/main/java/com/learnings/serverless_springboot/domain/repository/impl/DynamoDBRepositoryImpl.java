package com.learnings.serverless_springboot.domain.repository.impl;

import com.learnings.serverless_springboot.domain.entity.AbstractEntity;
import com.learnings.serverless_springboot.domain.repository.DynamoDBRepository;
import com.learnings.serverless_springboot.utils.EnvironmentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.ArrayList;
import java.util.List;

import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;

@Component
public class DynamoDBRepositoryImpl implements DynamoDBRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbClient dynamoDbClient;

    @Autowired
    public DynamoDBRepositoryImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient, DynamoDbClient dynamoDbClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public <T> DynamoDbTable<T> getTable(Class<T> clazz) {
        return getTableFromClass(clazz);
    }

    @Override
    public <T> T getItem(Class<T> clazz, String hashKey, String sortKey) {
        DynamoDbTable<T> table = getTableFromClass(clazz);

        Key key;
        if (sortKey != null) {
            key = Key.builder().partitionValue(hashKey).sortValue(sortKey).build();
        } else {
            key = Key.builder().partitionValue(hashKey).build();
        }

        GetItemEnhancedRequest request = GetItemEnhancedRequest.builder().key(key).build();

        return table.getItem(request);
    }


    @Override
    public <T> List<T> queryTable(Class<T> clazz, String hashKey) {
        List<T> items = new ArrayList<>();

        DynamoDbTable<T> table = getTableFromClass(clazz);

        QueryEnhancedRequest request = QueryEnhancedRequest.builder().queryConditional(keyEqualTo(Key.builder().partitionValue(hashKey).build())).build();

        PageIterable<T> result = table.query(request);

        result.items().stream().forEach(items::add);

        return items;
    }

    @Override
    public <T> List<T> scanTable(Class<T> clazz) {
        List<T> items = new ArrayList<>();

        DynamoDbTable<T> table = getTableFromClass(clazz);

        ScanEnhancedRequest request = ScanEnhancedRequest.builder().build();

        PageIterable<T> result = table.scan(request);

        result.items().stream().forEach(items::add);

        return items;
    }

    @Override
    public <T> void createItem(Class<T> clazz, T item) {

        DynamoDbTable<T> table = getTableFromClass(clazz);

        table.putItem(PutItemEnhancedRequest.builder(clazz).item(item).build());

    }

    @Override
    public <T extends AbstractEntity> void updateItem(Class<T> clazz, T item) {
        DynamoDbTable<T> table = getTableFromClass(clazz);

        if (item.getSortKey() == null) {
            table.putItem(PutItemEnhancedRequest.builder(clazz).item(item).build());
            return;
        }

        table.updateItem(UpdateItemEnhancedRequest.builder(clazz).item(item).build());
    }

    @Override
    public <T> void deleteItem(Class<T> clazz, T item) {
        DynamoDbTable<T> table = getTableFromClass(clazz);

        table.deleteItem(item);
    }

    @Override
    public <T> List<T> queryGSI(Class<T> clazz, String hashKey, String sortKey, String secondaryIndexName) {
        List<T> items = new ArrayList<>();

        DynamoDbTable<T> table = getTableFromClass(clazz);
        DynamoDbIndex<T> customersByName = table.index(secondaryIndexName);

        SdkIterable<Page<T>> results =
                customersByName.query(r -> r.queryConditional(keyEqualTo(k -> k.partitionValue(hashKey).sortValue(sortKey))));

        PageIterable<T> pages = PageIterable.create(results);

        pages.items().stream().forEach(items::add);

        return items;
    }

    private <T>DynamoDbTable<T> getTableFromClass(Class<T> clazz) {
        return dynamoDbEnhancedClient.table(getTableName(clazz), TableSchema.fromBean(clazz));
    }

    /**
     * Table name will be of format {@code classSimpleName.toLowerCase().replace("entity", "")-${ENVIRONMENT}}
     */
    private String getTableName(Class<?> clazz) {
        String tableName = clazz.getSimpleName().toLowerCase().replace("entity", "");
        return tableName + "-table-" + EnvironmentUtil.getEnvironmentWithDefaultDev();
    }
}
