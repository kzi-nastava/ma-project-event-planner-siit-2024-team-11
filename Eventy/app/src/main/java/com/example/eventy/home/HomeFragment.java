package com.example.eventy.home;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventy.R;
import com.example.eventy.databinding.FragmentHomeBinding;
import com.example.eventy.home.events.EventsFragment;
import com.example.eventy.home.events.EventsViewModel;
import com.example.eventy.home.events.featured_events.FeaturedEventsFragment;
import com.example.eventy.home.events.featured_events.FeaturedEventsTitleFragment;
import com.example.eventy.register.RegisterOrganiserFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private EventsViewModel eventsViewModel;
    private boolean isFirstSelection = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // load Featured Events/Solutions title container
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_title, new FeaturedEventsTitleFragment())
                .commit();

        // Load Top 5 Events/Solutions container
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_view, new FeaturedEventsFragment())
                .commit();

        // Load All Events container
        getChildFragmentManager().beginTransaction()
                .replace(R.id.all_events, new EventsFragment())
                .commit();

        binding.tabEvent.setOnClickListener(v -> {
            binding.tabEvent.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_active_text_color));
            binding.tabEvent.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_active_background));

            binding.tabSolutions.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_inactive_text_color));
            binding.tabSolutions.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_inactive_background));

            Fragment featuredEventsTitleFragment = new FeaturedEventsTitleFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.tab_title, featuredEventsTitleFragment)
                    .addToBackStack(null)
                    .commit();

            Fragment fragmentFeaturedEvents = new FeaturedEventsFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.tab_view, fragmentFeaturedEvents)
                    .addToBackStack(null)
                    .commit();

            Fragment fragmentEvents = new EventsFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.all_events, fragmentEvents)
                    .addToBackStack(null)
                    .commit();
        });

        binding.tabSolutions.setOnClickListener(v -> {
            binding.tabEvent.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_inactive_text_color));
            binding.tabEvent.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_inactive_background));

            binding.tabSolutions.setTextColor(ContextCompat.getColor(v.getContext(), R.color.tab_active_text_color));
            binding.tabSolutions.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.home_tab_active_background));

            Fragment fragment = new RegisterOrganiserFragment();

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.tab_view, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        eventsViewModel = new ViewModelProvider(this).get(EventsViewModel.class);
        SearchView searchView = binding.searchInput;
        /* Posmatranje LiveData objekta i ažuriranje UI-a:
         * Ovaj deo koda dodaje observer na LiveData<String> objekat unutar
         * ProductsPageViewModel. Svaki put kada dođe do promene podatka
         * unutar LiveData objekta (u ovom slučaju searchText),
         * ta promena se automatski prosleđuje i aktivira se metoda
         * setQueryHint na SearchView komponenti sa novom vrednošću.
         * Funkcija getViewLifecycleOwner() garantuje da se observer
         * povezuje sa životnim ciklusom vlasnika prikaza fragmenta,
         * što znači da će se observer automatski ukloniti kada fragment
         * više nije vidljiv ili je uništen, sprečavajući time moguće curenje memorije.
         * */
        eventsViewModel.getText().observe(getViewLifecycleOwner(), searchView::setQueryHint);

        Button filterButton = binding.filterButton;
        filterButton.setOnClickListener(v -> {
            Toast.makeText(this.getContext(), "Filter button clicked!", Toast.LENGTH_SHORT).show();

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
            View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_event_filter, null);
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();
        });

        Spinner spinner = binding.sortButton;
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.event_sort_options));
        // Specify the layout to use when the list of choices appears
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Provera da li je ovo prvi poziv
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return; // Izlazak iz metode bez prikazivanja dijaloga
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("Change the sort option?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.v("ShopApp", (String) parent.getItemAtPosition(position));
                                ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = dialog.create();
                alert.show();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        return root;
    }


}