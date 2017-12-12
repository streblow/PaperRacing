package de.streblow.paperracing;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final Context context = this;
    private MainView mainView = null;
    private Handler waitHandler = null;

    private ArrayList<RaceStatsEntry> raceStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.imageButton1).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageButton2).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageButton3).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageButton4).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageButton5).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageButton6).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageButton7).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageButton8).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageButton9).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageButtonUndo).setVisibility(View.INVISIBLE);
        mainView = (MainView)findViewById(R.id.cvMainView);
        mainView.buttonHidden = true;
        waitHandler = new Handler();
        raceStats = new ArrayList<RaceStatsEntry>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        FragmentManager fm = null;
        Bundle arguments = null;
        switch (item.getItemId())
        {
            case R.id.action_quickrace:
                fm = getFragmentManager();
                NewRaceDialogFragment quickrace = new NewRaceDialogFragment();
                arguments = new Bundle();
                arguments.putString("title", getString(R.string.quickrace_title));
                quickrace.setArguments(arguments);
                quickrace.listener = new NewRaceDialogFragment.OnDialogFragmentDismissedListener() {
                    @Override
                    public void onDialogFragmentDismissedListener(int[] types, String[] names) {
                        if (types != null && names != null)
                            if (types.length > 0 && names.length > 0) {
                                setTitle(getString(R.string.main_activity_title) + " - " +
                                getString(R.string.quickrace_title));
                                mainView.init(MainActivity.this, types, names);
                                mainView.game.type = Game.QUICKRACE;
                                findViewById(R.id.imageButton1).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton2).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton3).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton4).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton5).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton6).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton7).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton8).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton9).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButtonUndo).setVisibility(View.VISIBLE);
                                mainView.firstRun = false;
                                mainView.buttonHidden = false;
                                waitHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startGame();
                                    }
                                }, 0);
                            }
                    }
                };
                quickrace.show(fm, "Quickrace");
                return true;
            case R.id.action_race:
                fm = getFragmentManager();
                NewRaceDialogFragment race = new NewRaceDialogFragment();
                arguments = new Bundle();
                arguments.putString("title", getString(R.string.race_title));
                race.setArguments(arguments);
                race.listener = new NewRaceDialogFragment.OnDialogFragmentDismissedListener() {
                    @Override
                    public void onDialogFragmentDismissedListener(int[] types, String[] names) {
                        if (types != null && names != null)
                            if (types.length > 0 && names.length > 0) {
                                setTitle(getString(R.string.main_activity_title) + " - " +
                                        getString(R.string.race_title));
                                mainView.init(MainActivity.this, types, names);
                                mainView.game.type = Game.RACE;
                                findViewById(R.id.imageButton1).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton2).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton3).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton4).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton5).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton6).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton7).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton8).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButton9).setVisibility(View.VISIBLE);
                                findViewById(R.id.imageButtonUndo).setVisibility(View.INVISIBLE);
                                mainView.firstRun = false;
                                mainView.buttonHidden = false;
                                waitHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startGame();
                                    }
                                }, 0);
                            }
                    }
                };
                race.show(fm, "Race");
                return true;
            case R.id.action_season:
                return true;
            case R.id.action_settings:
                // test TableView
                fm = getFragmentManager();
                TableDialogFragment raceTable = new TableDialogFragment();
                arguments = new Bundle();
                arguments.putString("header", "Player;Races;Won (%)");
                String data = "Lars Streblow;120;80 (66.6)";
                data += ";Anton;100;60 (60.0)";
                data += ";Bertold;100;60 (60.0)";
                data += ";Cristoph;100;60 (60.0)";
                data += ";Detlef;100;60 (60.0)";
                data += ";Erwin;100;60 (60.0)";
                data += ";Friedrich;100;60 (60.0)";
                data += ";Gustaf;100;60 (60.0)";
                data += ";Harald;100;60 (60.0)";
                data += ";Ingolf;100;60 (60.0)";
                data += ";Jürgen;100;60 (60.0)";
                data += ";Konrad;100;60 (60.0)";
                data += ";Lothar;100;60 (60.0)";
                data += ";Martin;100;60 (60.0)";
                arguments.putString("data", data);
                raceTable.setArguments(arguments);
                raceTable.show(fm, "RaceTable");
                return true;
            case R.id.action_help:
                HelpDialog help = new HelpDialog(this);
                help.setTitle(R.string.help_title);
                help.show();
                return true;
            case R.id.action_about:
                AboutDialog about = new AboutDialog(this);
                about.setTitle(R.string.about_title);
                about.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startGame() {
        if (!mainView.game.finished())
            animateMoves();
    }

    public void onImageButton1Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                mainView.game.currentplayer().move(Player.DOWNRIGHT);
                mainView.game.nextplayer();
                mainView.invalidate();
                if (!mainView.game.finished())
                    animateMoves();
            }
    }

    public void onImageButton2Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                mainView.game.currentplayer().move(Player.DOWN);
                mainView.game.nextplayer();
                mainView.invalidate();
                if (!mainView.game.finished())
                    animateMoves();
            }
    }

    public void onImageButton3Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                mainView.game.currentplayer().move(Player.DOWNLEFT);
                mainView.game.nextplayer();
                mainView.invalidate();
                if (!mainView.game.finished())
                    animateMoves();
            }
    }

    public void onImageButton4Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                mainView.game.currentplayer().move(Player.RIGHT);
                mainView.game.nextplayer();
                mainView.invalidate();
                if (!mainView.game.finished())
                    animateMoves();
            }
    }

    public void onImageButton5Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                mainView.game.currentplayer().move(Player.SAME);
                mainView.game.nextplayer();
                mainView.invalidate();
                if (!mainView.game.finished())
                    animateMoves();
            }
    }

    public void onImageButton6Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                mainView.game.currentplayer().move(Player.LEFT);
                mainView.game.nextplayer();
                mainView.invalidate();
                if (!mainView.game.finished())
                    animateMoves();
            }
    }

    public void onImageButton7Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                mainView.game.currentplayer().move(Player.UPRIGHT);
                mainView.game.nextplayer();
                mainView.invalidate();
                if (!mainView.game.finished())
                    animateMoves();
            }
    }

    public void onImageButton8Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                mainView.game.currentplayer().move(Player.UP);
                mainView.game.nextplayer();
                mainView.invalidate();
                if (!mainView.game.finished())
                    animateMoves();
            }
    }

    public void onImageButton9Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                mainView.game.currentplayer().move(Player.UPLEFT);
                mainView.game.nextplayer();
                mainView.invalidate();
                if (!mainView.game.finished())
                    animateMoves();
            }
    }

    public void onImageButtonUndo(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                do
                    mainView.game.undo();
                while (mainView.game.currentplayer().type() != Player.HUM);
                mainView.invalidate();
            }
    }

    public void animateMoves() {
        if (mainView.game.currentplayer().type() == Player.COM) {
            waitHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mainView.game.currentplayer().ask();
                    mainView.invalidate();
                    mainView.game.nextplayer();
                    if (!mainView.game.finished())
                        if (mainView.game.currentplayer().type() == Player.COM)
                            waitHandler.postDelayed(this, mainView.animationDuration);
                }
            }, mainView.animationDuration);
        }
        mainView.invalidate();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putInt("MATEINMOVES", mMateInMoves);
//        savedInstanceState.putBoolean("FIRSTMOVEONLY", mFirstMoveOnly);
//        BoardView boardView = (BoardView) findViewById(R.id.cvBoardView);
//        boardView.saveState(savedInstanceState);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

//        mAppMode = savedInstanceState.getInt("APPMODE");
//        mMateInMoves = savedInstanceState.getInt("MATEINMOVES");
//        mFirstMoveOnly = savedInstanceState.getBoolean("FIRSTMOVEONLY");
//        BoardView boardView = (BoardView) findViewById(R.id.cvBoardView);
//        boardView.restoreState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
//        saveState();
    }

    @Override
    public void onResume() {
        super.onResume();
//        restoreState();
    }


}