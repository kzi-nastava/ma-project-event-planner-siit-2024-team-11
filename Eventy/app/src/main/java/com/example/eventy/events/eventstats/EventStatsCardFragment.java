package com.example.eventy.events.eventstats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.eventy.R;
import com.example.eventy.home.events.event_card.EventCardFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class EventStatsCardFragment extends Fragment {

    private BarChart barChart;
    private PieChart averageGradeChart;

    public EventStatsCardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_stats_card, container, false);

        // Load card fragment dynamically
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.cardContainer, new EventCardFragment());  // Replace with actual card fragment
        transaction.commit();

        // Initialize charts
        barChart = view.findViewById(R.id.barChart);
        averageGradeChart = view.findViewById(R.id.averageGradeChart);

        setupBarChart();
        setupPieChart();

        return view;
    }

    private void setupBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, 10));
        entries.add(new BarEntry(2, 20));
        entries.add(new BarEntry(3, 15));
        entries.add(new BarEntry(4, 5));
        entries.add(new BarEntry(5, 8));

        BarDataSet dataSet = new BarDataSet(entries, "Grades Distribution");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void setupPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(4.2f, "Average Grade"));

        PieDataSet dataSet = new PieDataSet(entries, "Grade");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(dataSet);
        averageGradeChart.setData(pieData);
        averageGradeChart.invalidate();
    }
}