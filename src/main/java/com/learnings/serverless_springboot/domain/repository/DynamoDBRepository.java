package com.learnings.serverless_springboot.domain.repository;

import com.learnings.serverless_springboot.domain.entity.AbstractEntity;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;

public interface DynamoDBRepository {
    /**
     * Returns table for a given entity
     * @param clazz
     * @return
     * @param <T>
     */
    <T> DynamoDbTable<T> getTable(Class<T> clazz);

    /**
     * Query table using hashKey
     *
     */
    <T> List<T> queryTable(Class<T> clazz, String hashKey);

    /**
     * Scan table to get all items
     */
    <T> List<T> scanTable(Class<T> clazz);

    /**
     * Get an Item from the table using hashKey
     */
    <T> T getItem(Class<T> clazz, String hashKey, String sortKey);

    /**
     * Create an Item in the table
     */
    <T> void createItem(Class<T> clazz, T item);

    /**
     * Update an Item in the table
     */
    <T extends AbstractEntity> void updateItem(Class<T> clazz, T item);

    /**
     * Delete an Item in the table
     */
    <T> void deleteItem(Class<T> clazz, T item);

    /**
     * Query GSI
     */
    <T> List<T> queryGSI(Class<T> clazz, String hashKey, String sortKey, String secondaryIndexName);

}
