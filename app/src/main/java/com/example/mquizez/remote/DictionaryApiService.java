package com.example.mquizez.remote;

import com.example.mquizez.model.api.MyMemoryApiResponse;
import com.example.mquizez.model.api.WordApiResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DictionaryApiService {
    // API từ điển
    @GET("api/v2/entries/en/{word}")
    Call<List<WordApiResponse>> getWordDefinition(@Path("word") String word);

    // API DỊCH THUẬT MỚI
    // Endpoint: https://api.mymemory.translated.net/get?q=Hello%20World!&langpair=en|vi
    @GET("https://api.mymemory.translated.net/get")
    Call<MyMemoryApiResponse> translateText(
            @Query("q") String textToTranslate,
            @Query("langpair") String languagePair
    );
}
