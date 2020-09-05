package com.example.portal_film;

public class MovieModel {
    //--------------------------------------
    //Ambil data API, sumber :
    //1.com.example.portal_film.retrofit : https://square.github.io/retrofit/
    //2.gson : https://github.com/square/retrofit/tree/master/retrofit-converters/gson
    //3.Generator : json 2 pojo :
    //  a.Anotasi :
    //    glassfish javax annotation : https://bintray.com/bintray/jcenter/org.glassfish:javax.annotation
    //  b.Cara penggunaan :
    //    - Manual / alternatif : https://codebeautify.org/json-to-java-converter)
    //    - Plugin : preferences -> plugin -> search : RoboPOGenerator
    //--------------------------------------

    private String JudulFilm;
    private String posterfilm;

    //setter & getter
    //Klik kanan -> generate -> getter & setter


    public String getJudulFilm() {
        return JudulFilm;
    }

    public void setJudulFilm(String judulFilm) {
        JudulFilm = judulFilm;
    }

    public String getPosterfilm() {
        return posterfilm;
    }

    public void setPosterfilm(String posterfilm) {
        this.posterfilm = posterfilm;
    }
}
