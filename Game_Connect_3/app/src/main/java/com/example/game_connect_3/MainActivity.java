package com.example.game_connect_3;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

public class MainActivity extends AppCompatActivity {

    //1.Buat variabel yg dibutuhkan :
    //  a.Variabel penanda coin yg muncul sebelumnya,
    //    kondisi  :
    //    - yellow : 0,
    //    - red    : 1
    int active = 0;

    //  b.Variabel penanda sebuah imageView sudah di-klik atau belum (empty atau tidak)
    //    kondisi : klo belum di klik -> 2
    int [] gameState = {2,2,2,2,2,2,2,2,2};

    //  c.Variabel penanda klo menang
    //    kondisi : sudah ada 3 coin yg sama secara berurutan (vertical / horisontal / diagonal)
    //    berdasarkan nilai tag pada elemen ImageView
    int [][] win =
            {
            {0,1,2},{3,4,5},{6,7,8}, /*horisontal*/
            {0,3,6},{1,4,7},{2,5,8}, /*vertical*/
            {0,4,8},{2,4,6}          /*diagonal*/
    };

    //  d.Variabel penanda game masih boleh berjalan/tidak
    //    Game masih boleh dijalankan selama belum ada pemenang
    boolean gameActive = true;

    //2.Buat fungsi unt memunculkan coin
    public void dropIn(View v) {

        ImageView Alliv = (ImageView) v;//Ambil elemen image (coin)
        Log.i( "tag", Alliv.getTag().toString() );
        int Tappediv = Integer.parseInt( Alliv.getTag().toString() );//Ambil nilai Tag dari elemen yg di-klik

        //Cek (mencegah bug) :
        // 1.Apakah sudah ada coin yg di-klik sebelum'nya di tempat yg sama
        //   Fungsi : mencegah bug -> pemain bisa gonta ganti warna coin setelah klik di tempat yg sama
        // 2.Apakah sudah ada pemenang / belum
        //   Fungsi : mencegah pemenang lebih dari 1 (efek dari pengecekan no.1)
        if (gameState[Tappediv] == 2 && gameActive) {
            Alliv.setTranslationY( -1500 );//Tentukan animasi yg di-inginkan, misal : geser ke atas agar menghilang
            gameState[Tappediv] = active;//Ubah nilai dari index array gameState sesuai warna coin (bukan lagi angka 2)

            //Munculkan coin sesuai coin sblm'nya
            if (active == 0) {
                Alliv.setImageResource( R.drawable.red );
                active = 1;
            } else {
                Alliv.setImageResource( R.drawable.yellow );
                active = 0;
            }

            Alliv.animate().translationYBy( 1500 ).rotation( 3600 ).setDuration( 300 );

            //Kondisi menang :
            //Memenuhi variabel penanda win (Variabel no.1-c)
            for (int[] i : win) {
                if (gameState[i[0]] != 2 &&
                    gameState[i[0]] == gameState[i[1]] &&
                    gameState[i[1]] == gameState[i[2]]) {

                    String coins = "";
                    if (active == 0) {
                        coins = "Yellow";
                    } else {
                        coins = "Red";
                    }

                    //Bila sudah ada yg menang :
                    //-Game selesai, krn sudah ada pemenang
                    //-Munculkan elemen unt me-restart permainan
                    gameActive = false;
                    Button btn = (Button) findViewById( R.id.btnRestart );
                    TextView tv = (TextView) findViewById( R.id.tvWinner );
                    btn.setVisibility( v.VISIBLE );
                    tv.setVisibility( v.VISIBLE );
                    tv.setText( coins + " has won" );//Pesan warna coin pemenang
                }
            }
        }
    }


    //3.Buat fungsi untuk me-restart permainan
    public void restart(View v){
        //a.Sembunyikan elemen html, agar tidak ter-restart > 1 kali
        Button btn = (Button) findViewById( R.id.btnRestart );
        TextView tv = (TextView) findViewById( R.id.tvWinner );
        btn.setVisibility( v.INVISIBLE );
        tv.setVisibility( v.INVISIBLE );

        //b.Kosongan semua gambar pada GridLayout
        androidx.gridlayout.widget.GridLayout gl = (androidx.gridlayout.widget.GridLayout) findViewById( R.id.gridLayout );
        for(int j=0;j<gl.getChildCount();j++)
        {
            ImageView ivchild = (ImageView) gl.getChildAt(j);
            ivchild.setImageDrawable( null );
        }

        //c.Kembalikan variabel ke value default'nya
        for(int k=0;k<gameState.length;k++){gameState[k]=2;}
        active = 0;
        gameActive = true;
    }

    //Pengembangan :
    //Kondisi draw
    //Scoring
    //Penambahan jumlah pemain


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Button btn = (Button) findViewById( R.id.btnRestart );
        TextView tv = (TextView) findViewById( R.id.tvWinner );

        btn.setVisibility( View.INVISIBLE );
        tv.setVisibility( View.INVISIBLE );
        tv.setText(" ");


    }
}
