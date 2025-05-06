package com.example.carrito

import androidx.room.*

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    suspend fun getAll(): List<CartItem>

    @Insert
    suspend fun insert(item: CartItem)

    @Delete
    suspend fun delete(item: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}
