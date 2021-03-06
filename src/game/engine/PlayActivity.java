package game.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import game.config.R;
import game.states.HighscoreState;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Esta classe representa a classe de jogo, para esta classe foi usado o padrao
 * de desenho Singleton
 * 
 * @author Andr� Pires, Filipe Gama
 * @see Activity
 */
public class PlayActivity extends Activity {

	private SensorManager sensorManager;
	private MediaPlayer malfunction;
	private MediaPlayer health_powerup;
	private MediaPlayer fuel_powerup;
	private MediaPlayer nodamage;
	private boolean mute = false;
	private HighscoreState highscores;
	private static PlayActivity singleInstance = null;

	private MediaPlayer coinSound;

	/**
	 * @return the highscores
	 */
	public HighscoreState getHighscores() {
		return highscores;
	}

	/**
	 * @param highscores
	 *            the highscores to set
	 */
	public void setHighscores(HighscoreState highscores) {
		this.highscores = highscores;
	}

	public static PlayActivity getSingleInstance() {
		return singleInstance;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		GameView game_view = new GameView(getBaseContext());

		super.onCreate(savedInstanceState);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		malfunction = MediaPlayer.create(getBaseContext(), R.raw.malfunction);
		health_powerup = MediaPlayer.create(getBaseContext(), R.raw.powerup);
		fuel_powerup = MediaPlayer.create(getBaseContext(), R.raw.powerup2);
		nodamage = MediaPlayer.create(getBaseContext(), R.raw.nodamage);
		coinSound = MediaPlayer.create(getBaseContext(), R.raw.coin);

		if (mute)
			muteSounds();

		singleInstance = this;

		sensorManager.registerListener(Controllers.getControllerInstance(),
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(game_view);

		loadHighscores();
	}

	/**
	 * Guarda os highscores num ficheiro na memoria interna do telemovel
	 */
	public void saveHighscores() {
		try {
			FileOutputStream fos = openFileOutput(HighscoreActivity.filename,
					MODE_WORLD_READABLE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(highscores);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Funcao que permite perguntar o nome ao jogador atraves dum input
	 */
	public void askName() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("New Highscore!");
		alert.setMessage("Insert your name: ");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				addH(value, (int) GameLoop.getPoints());
				saveHighscores();
				saveCoins();
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				saveCoins();
			}
		});

		alert.show();
	}

	public void addH(String name, int score) {
		highscores.addHighscore(name, score);
	}

	/**
	 * Faz load dos highscores atraves do ficheiro guardado na memoria interna
	 * do telemovel
	 */
	public void loadHighscores() {
		File file = getFileStreamPath(HighscoreActivity.filename);

		if (!file.exists()) {
			highscores = new HighscoreState();
		}

		else {
			try {
				FileInputStream fis = openFileInput(HighscoreActivity.filename);
				ObjectInputStream ois = new ObjectInputStream(fis);
				highscores = (HighscoreState) ois.readObject();
				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void muteSounds() {
		malfunction.setVolume(0, 0);
		health_powerup.setVolume(0, 0);
		fuel_powerup.setVolume(0, 0);
		nodamage.setVolume(0, 0);
		coinSound.setVolume(0, 0);
	}

	@Override
	public void onBackPressed() {
		saveCoins();
		GameLoop.stopThread(0);
		super.onBackPressed();
	}

	public void saveCoins() {
		try {
			FileOutputStream fos = openFileOutput(MarketActivity.filenameCoins,MODE_WORLD_READABLE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(MarketActivity.getCoins());
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void playMalfunc() {
		malfunction.start();
	}

	public void playHealth() {
		health_powerup.start();
	}

	public void playCoin() {
		coinSound.start();
	}

	public void playFuel() {
		fuel_powerup.start();
	}

	public void playNoDamage() {
		nodamage.start();
	}

	public boolean isMute() {
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}
}
