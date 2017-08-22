package net.luxvacuos.lightengine.demo.ui;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;

import java.util.ArrayList;
import java.util.List;

import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.Box;
import net.luxvacuos.lightengine.client.ui.Button;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Container;
import net.luxvacuos.lightengine.client.ui.Text;
import net.luxvacuos.lightengine.client.util.Maths;

public class RectGL extends ComponentWindow {

	private List<Ball> selectedBalls = new ArrayList<>();
	private Container balls;
	private int points;
	private Text pointsText;

	public RectGL(int x, int y, int w, int h) {
		super(x, y, w, h, "Rectball");
	}

	@Override
	public void initApp() {
		super.initApp();
		super.setResizable(false);
		super.setBackgroundColor("#4A6563FF");
		super.toggleTitleBar();
		super.setDecorations(false);

		Box helpBox = new Box(10, -20, 60, 50);
		helpBox.setColor("#FFD229FF");
		helpBox.setWindowAlignment(Alignment.LEFT_TOP);
		helpBox.setAlignment(Alignment.RIGHT_BOTTOM);
		helpBox.setLeftTop(5f);
		helpBox.setRightTop(5f);
		Box exitBox = new Box(-10, -20, 60, 50);
		exitBox.setColor("#FFD229FF");
		exitBox.setWindowAlignment(Alignment.RIGHT_TOP);
		exitBox.setAlignment(Alignment.LEFT_BOTTOM);
		exitBox.setLeftTop(5f);
		exitBox.setRightTop(5f);

		Button help = new Button(20, -30, 40, 40, "");
		help.setWindowAlignment(Alignment.LEFT_TOP);
		help.setAlignment(Alignment.RIGHT_BOTTOM);
		Button exit = new Button(-20, -30, 40, 40, "");
		exit.setWindowAlignment(Alignment.RIGHT_TOP);
		exit.setAlignment(Alignment.LEFT_BOTTOM);

		Box topB1 = new Box(0, -90, 460, 40);
		topB1.setAlignment(Alignment.CENTER);
		topB1.setWindowAlignment(Alignment.TOP);
		topB1.setColor("#FFD229FF");
		topB1.setLeftBottom(5f);
		topB1.setRightBottom(5f);
		Box topB2 = new Box(0, -140, 260, 60);
		topB2.setAlignment(Alignment.CENTER);
		topB2.setWindowAlignment(Alignment.TOP);
		topB2.setColor("#FFD229FF");
		topB2.setLeftBottom(5f);
		topB2.setRightBottom(5f);
		Box topB3 = new Box(0, -135, 240, 50);
		topB3.setAlignment(Alignment.CENTER);
		topB3.setWindowAlignment(Alignment.TOP);
		topB3.setColor("#000000FF");

		pointsText = new Text("" + this.points, 0, -138);
		pointsText.setAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		pointsText.setWindowAlignment(Alignment.TOP);
		pointsText.setFontSize(80);

		Container top = new Container(0, 0, w, 200);
		top.setWindowAlignment(Alignment.TOP);
		top.setAlignment(Alignment.BOTTOM);

		top.addComponent(helpBox);
		top.addComponent(help);
		top.addComponent(exitBox);
		top.addComponent(exit);
		top.addComponent(topB1);
		top.addComponent(topB2);
		top.addComponent(topB3);
		top.addComponent(pointsText);

		balls = new Container(0, 0, w, w);
		for (int x = 0; x < 6; x++) {
			for (int y = 0; y < 6; y++) {
				int type = Maths.randInt(0, 3);
				Ball ball = new Ball(8 + x * (w / 6), 8 + y * (w / 6), 64, 64, type);
				ball.setOnButtonPress(() -> {
					this.selectedBalls.add(ball);
				});
				balls.addComponent(ball);
			}
		}

		super.addComponent(top);
		super.addComponent(balls);
	}

	@Override
	public void updateApp(float delta) {
		super.updateApp(delta);
		if (selectedBalls.size() >= 4) {
			int type = selectedBalls.get(0).getType();
			if (selectedBalls.get(1).getType() == type && selectedBalls.get(2).getType() == type
					&& selectedBalls.get(3).getType() == type) {
				balls.removeComponent(selectedBalls.get(0));
				balls.removeComponent(selectedBalls.get(1));
				balls.removeComponent(selectedBalls.get(2));
				balls.removeComponent(selectedBalls.get(3));
				points += 6;
				pointsText.setText("" + points);
				selectedBalls.clear();
			}
		}
	}

}
