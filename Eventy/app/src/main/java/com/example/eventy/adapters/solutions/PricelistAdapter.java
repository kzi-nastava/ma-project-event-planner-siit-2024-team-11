package com.example.eventy.adapters.solutions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventy.R;
import com.example.eventy.solutions.PricelistItemEditDialog;
import com.example.eventy.solutions.model.PricelistItem;
import com.example.eventy.utils.ClientUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricelistAdapter extends RecyclerView.Adapter<PricelistAdapter.PricelistItemViewHolder> {

    private List<PricelistItem> pricelistItems;
    private LayoutInflater layoutInflater;
    private Context context;

    public PricelistAdapter(Context context, List<PricelistItem> items) {
        this.context = context;
        this.pricelistItems = items;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PricelistItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.pricelist_item_card, parent, false);

        return new PricelistItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PricelistItemViewHolder holder, int position) {
        PricelistItem item = pricelistItems.get(position);
        holder.id = item.getId();
        Double itemPrice = item.getPrice();
        Double itemDiscount = item.getDiscount();
        Double itemFinalPrice = itemPrice - (itemPrice * itemDiscount / 100);
        holder.name.setText(item.getName());
        holder.price.setText(String.format("%.2f", itemPrice));
        holder.discount.setText(String.format("%.2f", itemDiscount));
        holder.finalPrice.setText(String.format("%.2f", itemFinalPrice));

        holder.editButton.setOnClickListener(v -> {
            PricelistItemEditDialog dialog = new PricelistItemEditDialog(holder.itemView.getContext(), holder.id, holder.name.getText().toString(), itemPrice, itemDiscount, (idValue, priceValue, discountValue) -> {
                Call<PricelistItem> call = ClientUtils.solutionService.updatePrice(new PricelistItem(idValue, holder.name.getText().toString(), priceValue, discountValue));
                call.enqueue(new Callback<PricelistItem>() {
                    @Override
                    public void onResponse(Call<PricelistItem> call, Response<PricelistItem> response) {
                        if (response.isSuccessful()) {
                            holder.price.setText(String.format("%.2f", priceValue));
                            holder.discount.setText(String.format("%.2f", discountValue));
                            Double newFinalPrice = priceValue - (priceValue * discountValue / 100);
                            holder.finalPrice.setText(String.format("%.2f", newFinalPrice));
                            Toast.makeText(holder.itemView.getContext(), "Price for " + item.getName() + " updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "Error with updating price!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PricelistItem> call, Throwable t) {
                        Toast.makeText(holder.itemView.getContext(), "Connection error!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            dialog.show();
        });
    }

    @Override
    public int getItemCount() { return pricelistItems.size(); }

    public static class PricelistItemViewHolder extends RecyclerView.ViewHolder {
        Long id;
        TextView name;
        TextView price;
        TextView discount;
        TextView finalPrice;
        ImageButton editButton;

        public PricelistItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.pricelist_item_name);
            price = itemView.findViewById(R.id.pricelist_item_price);
            discount = itemView.findViewById(R.id.pricelist_item_discount);
            finalPrice = itemView.findViewById(R.id.pricelist_item_final_price);
            editButton = itemView.findViewById(R.id.pricelist_item_edit_button);
        }
    }
}
