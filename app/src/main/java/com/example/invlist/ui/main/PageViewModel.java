package com.example.invlist.ui.main;

import com.example.invlist.components.InvComponent;
import com.example.invlist.components.InvFactory;
import com.example.invlist.components.InvType;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    /*private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return getTabContent(InvType.valueOf(input));
        }
    });

    private String getTabContent(InvType invType) {
        InvComponent invComponent = InvFactory.getInvComponent(invType);
        return (invComponent != null) ? invComponent.values() : "";
    }

    public void setIndex(int index) {
        System.out.println("Setting index: " + index);
        mText = Transformations.map(mIndex, new Function<Integer, String>() {
            @Override
            public String apply(Integer input) {
                return getTabContent(InvType.valueOf(input));
            }
        });
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        System.out.println("REturning text for: " + mIndex.getValue());
        return mText;
    }*/
}