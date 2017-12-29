package de.streblow.paperracing;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final Context context = this;
    private MainView mainView = null;
    private Handler waitHandler = null;
    private int buttonClicked = 0;
    private boolean saveClick = true;

    private ArrayList<RaceStatsEntry> raceStats;

    private final String paperRacerData = "PaperRacerData";

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
        buttonClicked = 0;
        raceStats = new ArrayList<>();
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
                arguments.putString("title", getString(R.string.quickrace_new_title));
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
                                resetButtons();
                                waitHandler.removeCallbacksAndMessages(null);
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
                arguments.putString("title", getString(R.string.race_new_title));
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
                                resetButtons();
                                waitHandler.removeCallbacksAndMessages(null);
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
            case R.id.action_racestats:
                showRaceStats();
                return true;
            case R.id.action_season:
                return true;
            case R.id.action_settings:
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

    public void resetButtons() {
        resetButton(1);
        resetButton(2);
        resetButton(3);
        resetButton(4);
        resetButton(5);
        resetButton(6);
        resetButton(7);
        resetButton(8);
        resetButton(9);
    }

    public void resetButton(int n) {
        ImageButton btn;
        switch (n) {
            case 1:
                btn = (ImageButton)findViewById(R.id.imageButton1);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button));
                break;
            case 2:
                btn = (ImageButton)findViewById(R.id.imageButton2);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button));
                break;
            case 3:
                btn = (ImageButton)findViewById(R.id.imageButton3);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button));
                break;
            case 4:
                btn = (ImageButton)findViewById(R.id.imageButton4);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button));
                break;
            case 5:
                btn = (ImageButton)findViewById(R.id.imageButton5);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button));
                break;
            case 6:
                btn = (ImageButton)findViewById(R.id.imageButton6);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button));
                break;
            case 7:
                btn = (ImageButton)findViewById(R.id.imageButton7);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button));
                break;
            case 8:
                btn = (ImageButton)findViewById(R.id.imageButton8);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button));
                break;
            case 9:
                btn = (ImageButton)findViewById(R.id.imageButton9);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button));
                break;
            default:
                break;
        }
    }

    public void setButtonYes(int n) {
        ImageButton btn;
        switch (n) {
            case 1:
                btn = (ImageButton)findViewById(R.id.imageButton1);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_yes));
                break;
            case 2:
                btn = (ImageButton)findViewById(R.id.imageButton2);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_yes));
                break;
            case 3:
                btn = (ImageButton)findViewById(R.id.imageButton3);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_yes));
                break;
            case 4:
                btn = (ImageButton)findViewById(R.id.imageButton4);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_yes));
                break;
            case 5:
                btn = (ImageButton)findViewById(R.id.imageButton5);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_yes));
                break;
            case 6:
                btn = (ImageButton)findViewById(R.id.imageButton6);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_yes));
                break;
            case 7:
                btn = (ImageButton)findViewById(R.id.imageButton7);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_yes));
                break;
            case 8:
                btn = (ImageButton)findViewById(R.id.imageButton8);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_yes));
                break;
            case 9:
                btn = (ImageButton)findViewById(R.id.imageButton9);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_yes));
                break;
            default:
                break;
        }
    }

    public void setButtonNo(int n) {
        ImageButton btn;
        switch (n) {
            case 1:
                btn = (ImageButton)findViewById(R.id.imageButton1);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_no));
                break;
            case 2:
                btn = (ImageButton)findViewById(R.id.imageButton2);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_no));
                break;
            case 3:
                btn = (ImageButton)findViewById(R.id.imageButton3);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_no));
                break;
            case 4:
                btn = (ImageButton)findViewById(R.id.imageButton4);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_no));
                break;
            case 5:
                btn = (ImageButton)findViewById(R.id.imageButton5);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_no));
                break;
            case 6:
                btn = (ImageButton)findViewById(R.id.imageButton6);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_no));
                break;
            case 7:
                btn = (ImageButton)findViewById(R.id.imageButton7);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_no));
                break;
            case 8:
                btn = (ImageButton)findViewById(R.id.imageButton8);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_no));
                break;
            case 9:
                btn = (ImageButton)findViewById(R.id.imageButton9);
                btn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_no));
                break;
            default:
                break;
        }
    }

    public void updateRaceStats() {
        if (mainView.game.type != Game.RACE)
            return;
        if (mainView.game.getplayercount() < 2)
            return;
        Boolean computerRace = true;
        for (int i = 0; i < mainView.game.getplayercount(); i++)
            if (mainView.game.getplayer(i).type() == Player.HUM) {
                computerRace = false;
                break;
            }
        if (!computerRace) {
            // update all human players race count
            for (int i = 0; i < mainView.game.getplayercount(); i++)
                if (mainView.game.getplayer(i).type() == Player.HUM) {
                    String name = mainView.game.getplayer(i).getname();
                    Boolean exists = false;
                    int index = -1;
                    for (int j = 0; j < raceStats.size(); j++)
                        if (raceStats.get(j).playerName.equalsIgnoreCase(name)) {
                            exists = true;
                            index = j;
                            break;
                        }
                    if (exists)
                        raceStats.get(index).races++;
                    else
                        raceStats.add(new RaceStatsEntry(name, 1, 0));
                }
            // update winners win count if is human
            if (mainView.game.getplayer(mainView.game.winner).type() == Player.HUM) {
                String name = mainView.game.getplayer(mainView.game.winner).getname();
                for (int i = 0; i < raceStats.size(); i++)
                    if (raceStats.get(i).playerName.equalsIgnoreCase(name)) {
                        raceStats.get(i).won++;
                        break;
                    }
            }
            // show race stats
            showRaceStats();
        }
    }

    public void showRaceStats() {
        FragmentManager fm = null;
        Bundle arguments = null;
        fm = getFragmentManager();
        TableDialogFragment raceTable = new TableDialogFragment();
        arguments = new Bundle();
        arguments.putString("header", getString(R.string.racetable_header_player) + ";" +
                getString(R.string.racetable_header_races) + ";" +
                getString(R.string.racetable_header_won));
        String data = "";
        for (int i = 0; i < raceStats.size(); i++) {
            if (i != 0)
                data += ";";
            float ratewon = 100.0f * ((float)raceStats.get(i).won /
                    (float)Math.max(raceStats.get(i).races, 1));
            String rate = String.format("%.1f", ratewon);
            data += raceStats.get(i).playerName + ";" +
                    raceStats.get(i).races + ";" +
                    raceStats.get(i).won + " (" +
                    rate + ")";
        }
        arguments.putString("data", data);
        arguments.putString("currentplayer", mainView.game.nameOfFavoritePlayer);
        arguments.putInt("width", getWindow().getAttributes().width);
        arguments.putInt("height", getWindow().getAttributes().height);
        raceTable.setArguments(arguments);
        raceTable.show(fm, "RaceTable");
    }

    public void clearRaceStats() {
        raceStats.clear();
    }

    public void onImageButton1Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                if (buttonClicked == 1 || !saveClick) {
                    buttonClicked = 0;
                    if (saveClick)
                        resetButton(1);
                    mainView.game.currentplayer().move(Player.DOWNRIGHT);
                    mainView.game.nextplayer();
                    mainView.invalidate();
                    if (!mainView.game.finished())
                        animateMoves();
                    else
                        updateRaceStats();
                } else {
                    if (buttonClicked != 0)
                        resetButton(buttonClicked);
                    buttonClicked = 1;
                    setButtonYes(1);
                }
            }
    }

    public void onImageButton2Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                if (buttonClicked == 2 || !saveClick) {
                    buttonClicked = 0;
                    if (saveClick)
                        resetButton(2);
                    mainView.game.currentplayer().move(Player.DOWN);
                    mainView.game.nextplayer();
                    mainView.invalidate();
                    if (!mainView.game.finished())
                        animateMoves();
                    else
                        updateRaceStats();
                } else {
                    if (buttonClicked != 0)
                        resetButton(buttonClicked);
                    buttonClicked = 2;
                    setButtonYes(2);
                }
            }
    }

    public void onImageButton3Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                if (buttonClicked == 3 || !saveClick) {
                    buttonClicked = 0;
                    if (saveClick)
                        resetButton(3);
                    mainView.game.currentplayer().move(Player.DOWNLEFT);
                    mainView.game.nextplayer();
                    mainView.invalidate();
                    if (!mainView.game.finished())
                        animateMoves();
                    else
                        updateRaceStats();
                } else {
                    if (buttonClicked != 0)
                        resetButton(buttonClicked);
                    buttonClicked = 3;
                    setButtonYes(3);
                }
            }
    }

    public void onImageButton4Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                if (buttonClicked == 4 || !saveClick) {
                    buttonClicked = 0;
                    if (saveClick)
                        resetButton(4);
                    mainView.game.currentplayer().move(Player.RIGHT);
                    mainView.game.nextplayer();
                    mainView.invalidate();
                    if (!mainView.game.finished())
                        animateMoves();
                    else
                        updateRaceStats();
                } else {
                    if (buttonClicked != 0)
                        resetButton(buttonClicked);
                    buttonClicked = 4;
                    setButtonYes(4);
                }
            }
    }

    public void onImageButton5Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                if (buttonClicked == 5 || !saveClick) {
                    buttonClicked = 0;
                    if (saveClick)
                        resetButton(5);
                    mainView.game.currentplayer().move(Player.SAME);
                    mainView.game.nextplayer();
                    mainView.invalidate();
                    if (!mainView.game.finished())
                        animateMoves();
                    else
                        updateRaceStats();
                } else {
                    if (buttonClicked != 0)
                        resetButton(buttonClicked);
                    buttonClicked = 5;
                    setButtonYes(5);
                }
            }
    }

    public void onImageButton6Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                if (buttonClicked == 6 || !saveClick) {
                    buttonClicked = 0;
                    if (saveClick)
                        resetButton(6);
                    mainView.game.currentplayer().move(Player.LEFT);
                    mainView.game.nextplayer();
                    mainView.invalidate();
                    if (!mainView.game.finished())
                        animateMoves();
                    else
                        updateRaceStats();
                } else {
                    if (buttonClicked != 0)
                        resetButton(buttonClicked);
                    buttonClicked = 6;
                    setButtonYes(6);
                }
            }
    }

    public void onImageButton7Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                if (buttonClicked == 7 || !saveClick) {
                    buttonClicked = 0;
                    if (saveClick)
                        resetButton(7);
                    mainView.game.currentplayer().move(Player.UPRIGHT);
                    mainView.game.nextplayer();
                    mainView.invalidate();
                    if (!mainView.game.finished())
                        animateMoves();
                    else
                        updateRaceStats();
                } else {
                    if (buttonClicked != 0)
                        resetButton(buttonClicked);
                    buttonClicked = 7;
                    setButtonYes(7);
                }
            }
    }

    public void onImageButton8Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                if (buttonClicked == 8 || !saveClick) {
                    buttonClicked = 0;
                    if (saveClick)
                        resetButton(8);
                    mainView.game.currentplayer().move(Player.UP);
                    mainView.game.nextplayer();
                    mainView.invalidate();
                    if (!mainView.game.finished())
                        animateMoves();
                    else
                        updateRaceStats();
                } else {
                    if (buttonClicked != 0)
                        resetButton(buttonClicked);
                    buttonClicked = 8;
                    setButtonYes(8);
                }
            }
    }

    public void onImageButton9Click(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (mainView.game.currentplayer().type() == Player.HUM) {
                if (buttonClicked == 9 || !saveClick) {
                    buttonClicked = 0;
                    if (saveClick)
                        resetButton(9);
                    mainView.game.currentplayer().move(Player.UPLEFT);
                    mainView.game.nextplayer();
                    mainView.invalidate();
                    if (!mainView.game.finished())
                        animateMoves();
                    else
                        updateRaceStats();
                } else {
                    if (buttonClicked != 0)
                        resetButton(buttonClicked);
                    buttonClicked = 9;
                    setButtonYes(9);
                }
            }
    }

    public void onImageButtonUndo(View v) {
        if (!mainView.game.finished() && !mainView.firstRun)
            if (saveClick) {
                buttonClicked = 0;
                resetButtons();
            }
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
                    if (!mainView.game.finished()) {
                        if (mainView.game.currentplayer().type() == Player.COM)
                            waitHandler.postDelayed(this, mainView.animationDuration);
                    } else
                        updateRaceStats();
                }
            }, mainView.animationDuration);
        }
        mainView.invalidate();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        if (raceStats.size() > 0) {
            savedInstanceState.putInt("racelistlength", raceStats.size());
            for (int i = 0; i < raceStats.size(); i++) {
                savedInstanceState.putString("racelistentry" + i,
                    raceStats.get(i).playerName + ";" +
                    raceStats.get(i).races + ";" +
                    raceStats.get(i).won + ";" );
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        if (raceStats == null)
            raceStats = new ArrayList<>();
        else
            raceStats.clear();
        int length = savedInstanceState.getInt("racelistlength", 0);
        for (int i = 0; i < length; i++) {
            String entryStr = savedInstanceState.getString("racelistentry" + i);
            String[] split = entryStr.split(";");
            RaceStatsEntry entry = new RaceStatsEntry(split[0],
                Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            raceStats.add(entry);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreState();
    }

    public void saveState() {
        DataOutputStream dos = null;
        try {
            File file = new File(getFilesDir(), paperRacerData);
            dos = new DataOutputStream(new FileOutputStream(file));
            if (raceStats.size() > 0) {
                dos.writeInt(raceStats.size());
                for (int i = 0; i < raceStats.size(); i++) {
                    dos.writeUTF(raceStats.get(i).playerName + ";" +
                        raceStats.get(i).races + ";" +
                        raceStats.get(i).won + ";" );
                }
            }
            dos.close();
            dos = null;
        } catch (Exception e) {
            e.printStackTrace();
            if (dos != null)
                try {
                    dos.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
        }
    }

    public void restoreState() {
        DataInputStream dis = null;
        try {
            File file = new File(getFilesDir(), paperRacerData);
            dis = new DataInputStream(new FileInputStream(file));
            if (raceStats == null)
                raceStats = new ArrayList<>();
            else
                raceStats.clear();
            int length = dis.readInt();
            for (int i = 0; i < length; i++) {
                String entryStr = dis.readUTF();
                String[] split = entryStr.split(";");
                RaceStatsEntry entry = new RaceStatsEntry(split[0],
                        Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                raceStats.add(entry);
            }
            dis.close();
            dis = null;
        } catch (Exception e) {
            e.printStackTrace();
            if (dis != null)
                try {
                    dis.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
        }
    }

}