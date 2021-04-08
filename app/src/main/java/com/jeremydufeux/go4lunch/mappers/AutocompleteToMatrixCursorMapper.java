package com.jeremydufeux.go4lunch.mappers;

import android.database.MatrixCursor;

import com.jeremydufeux.go4lunch.models.googlePlaceAutocomplete.PlaceAutocomplete;
import com.jeremydufeux.go4lunch.models.googlePlaceAutocomplete.Prediction;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

import static com.jeremydufeux.go4lunch.repositories.GooglePlacesRepository.PLACE_TYPE;
import static com.jeremydufeux.go4lunch.ui.MainActivity.CURSOR_MATRIX_ID;
import static com.jeremydufeux.go4lunch.ui.MainActivity.CURSOR_MATRIX_NAME;

public class AutocompleteToMatrixCursorMapper implements Function<PlaceAutocomplete, MatrixCursor>{

    @Override
    public MatrixCursor apply(@NonNull PlaceAutocomplete placeAutocomplete) {
        String[] columns = {"_id", CURSOR_MATRIX_NAME, CURSOR_MATRIX_ID};
        MatrixCursor cursor = new MatrixCursor(columns);

        int i = 0;
        for (Prediction prediction : placeAutocomplete.getPredictions()){
            if(prediction.getTypes().contains(PLACE_TYPE)){
                cursor.addRow(new Object[]{i++, prediction.getDescription(), prediction.getPlaceId()});
            }
        }

        return cursor;
    }
}
