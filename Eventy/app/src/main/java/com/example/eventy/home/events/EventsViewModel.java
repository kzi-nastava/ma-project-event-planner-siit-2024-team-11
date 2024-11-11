package com.example.eventy.home.events;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventsViewModel extends ViewModel {
    /*
     * searchText je instanca MutableLiveData, generičke klase koja drži
     * neki tip podatka, u ovom slučaju String. MutableLiveData je
     * lifecycle-aware observable klasa, što znači da UI komponente
     * (kao što su Activity ili Fragment) mogu posmatrati (observe) promene
     * u ovim podacima i biti automatski obaveštene kada do promene dođe.
     * Posmatranje LiveData objekata omogućava UI komponentama
     * da reaguju na promene podataka, ažurirajući se prema potrebi.
     * */
    private final MutableLiveData<String> searchText;

    public EventsViewModel(){
        searchText = new MutableLiveData<>();
        searchText.setValue("This is search help!");
    }

    public LiveData<String> getText(){
        return searchText;
    }
}
