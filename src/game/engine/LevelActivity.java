package game.engine;

import game.config.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class LevelActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (Tools.getLevel() == 1)
			setContentView(R.layout.level1);
		else if (Tools.getLevel() == 2)
			setContentView(R.layout.level2);
		else if (Tools.getLevel() == 3)
			setContentView(R.layout.level3);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		GameLoop.getCurrent_instance().refresh();
		finish();
	}
}
