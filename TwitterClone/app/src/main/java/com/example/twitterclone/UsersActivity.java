package com.example.twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    ArrayList<String> users = new ArrayList<String>(  );
    ArrayAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_users );
        setTitle( "User List" );

        listView = findViewById( R.id.UsersListView );

        //1.Ambil data dari ParseServer
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo( "username",ParseUser.getCurrentUser().getUsername() );//agar list yg tampil bukan yg sedang login
        query.findInBackground( new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                //Ambil semua user'nya
                if(e == null && objects.size() > 0){
                    for(ParseUser user : objects){ users.add( user.getUsername() ); }
                    adapter.notifyDataSetChanged();

                    //tampilkan (checked di checkbox) semua user yg di-Follow
                    //di checkbox : true -> checked, false -> unchecked
                    for(String username : users ){
                        if(ParseUser.getCurrentUser().getList( "isFollowing" ).contains(username )){
                            listView.setItemChecked( users.indexOf( username ),true );
                        }else {}
                    }

                }else {Toast.makeText( getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT ).show();}
            }
        } );

        //2.Isi listView dengan data users, & checkbox
        adapter = new ArrayAdapter( this,android.R.layout.simple_list_item_checked,users );
        listView.setAdapter( adapter );
        listView.setChoiceMode( AbsListView.CHOICE_MODE_MULTIPLE );
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CheckedTextView checkedTextView = (CheckedTextView) view;
                if(checkedTextView.isChecked()){
                    //Follow -> langkah :
                    //cukup dengan add()
                    Toast.makeText( getApplicationContext(),users.get( position ) +" Followed",Toast.LENGTH_SHORT ).show();
                    ParseUser.getCurrentUser().add( "isFollowing",users.get( position ) );
                }else {
                    //UnFollow -> langkah :
                    //a.Ambil list dari semua user yg di-Follow, lalu remove yg di-unFollow
                    //b.Buat variabel list baru : berisi sisa user yg masih di-Follow
                    //c.Hapus semua user yg masih di-Follow
                    //d.Isi user yg di-Follow dengan list dari point (b)
                    Toast.makeText( getApplicationContext(),users.get( position ) +" UnFollowed",Toast.LENGTH_SHORT ).show();
                    ParseUser.getCurrentUser().getList( "isFollowing" ).remove( users.get( position ) );
                    List tempUsers = ParseUser.getCurrentUser().getList( "isFollowing" );
                    ParseUser.getCurrentUser().remove( "isFollowing" );
                    ParseUser.getCurrentUser().put("isFollowing", tempUsers );
                }
                ParseUser.getCurrentUser().saveInBackground( );
            }
        } );
    }

    //3.Menambahkan Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater( this );
        menuInflater.inflate( R.menu.tweet_menu,menu );
        return super.onCreateOptionsMenu( menu );
    }

    //Saat salah satu item dari menu dipilih user
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //A.Tweet
        if(item.getItemId() == R.id.tweet){

            //Tweet lewat dialog
            AlertDialog.Builder builder = new AlertDialog.Builder( this );
            builder.setTitle( "Send a Tweet" );
            final EditText tweetEditText = new EditText( this ); //buat komponent baru, yaitu EditText
            builder.setView( tweetEditText );


            builder.setPositiveButton( "Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Buat class baru & simpan tweet ke dlm ParseServer
                    ParseObject tweet = new ParseObject( "Tweet" );
                    tweet.put("tweet", tweetEditText.getText().toString() );
                    tweet.put( "username",ParseUser.getCurrentUser().getUsername() );
                    tweet.saveInBackground( new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){ Toast.makeText( getApplicationContext(), "Your tweet : " + tweetEditText.getText().toString(),Toast.LENGTH_SHORT ).show(); }
                            else { Toast.makeText( getApplicationContext(), e.getMessage(),Toast.LENGTH_SHORT ).show(); }
                        }
                    } );
                    //
                }
            } );

            builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText( getApplicationContext(), "Tweet Canceled " ,Toast.LENGTH_SHORT ).show();
                    dialog.cancel();//Agar keluar dari dialog (supaya user tidak stack hanya di dialog saja)
                }
            } );
            builder.show();
        }

        //B.Your Tweet
        else if (item.getItemId() == R.id.viewFeed){
            Intent intent = new Intent( this,FeedActivity.class );

            startActivity( intent );
        }

        //C.Logout
        else if (item.getItemId() == R.id.logout){
            Intent intent = new Intent( this,MainActivity.class );
            ParseUser.logOut();
            startActivity( intent );
        }

        return super.onOptionsItemSelected( item );
    }

    //Saat tekan Back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ParseUser.logOut();
    }
}