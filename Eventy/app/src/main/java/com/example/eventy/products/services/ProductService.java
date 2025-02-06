package com.example.eventy.products.services;

import com.example.eventy.products.model.CreateProduct;
import com.example.eventy.products.model.Product;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductService {
    String prefix = "products";

    @POST(prefix)
    Call<Product> create(@Body CreateProduct createProduct);

    @PUT(prefix + "/{id}")
    Call<Product> update(@Path("id") Long id, @Body Product product);
}
