package com.example.dictionary.DictionaryModel


import com.google.gson.annotations.SerializedName

data class DictionaryModelItem(
    @SerializedName("meanings")
    var meanings: List<Meaning>,
    @SerializedName("origin")
    var origin: String,
    @SerializedName("phonetic")
    var phonetic: String,
    @SerializedName("phonetics")
    var phonetics: List<Phonetic>,
    @SerializedName("word")
    var word: String
) {
    data class Meaning(
        @SerializedName("definitions")
        var definitions: List<Definition>,
        @SerializedName("partOfSpeech")
        var partOfSpeech: String
    ) {
        data class Definition(
            @SerializedName("antonyms")
            var antonyms: List<Any>,
            @SerializedName("definition")
            var definition: String,
            @SerializedName("example")
            var example: String,
            @SerializedName("synonyms")
            var synonyms: List<String>
        )
    }

    data class Phonetic(
        @SerializedName("audio")
        var audio: String,
        @SerializedName("text")
        var text: String
    )
}