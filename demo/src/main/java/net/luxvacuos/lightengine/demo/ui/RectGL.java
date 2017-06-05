package net.luxvacuos.lightengine.demo.ui;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.*;

import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.Box;
import net.luxvacuos.lightengine.client.ui.Button;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Container;
import net.luxvacuos.lightengine.client.ui.Text;

public class RectGL extends ComponentWindow {

	public RectGL(float x, float y, float w, float h) {
		super(x, y, w, h, "Rectball");
	}

	@Override
	public void initApp(Window window) {
		super.setResizable(false);
		super.setBackgroundColor("#4A6563FF");

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
		
		Text points = new Text("0000", 0, -138);
		points.setAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		points.setWindowAlignment(Alignment.TOP);
		points.setFontSize(80);

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
		top.addComponent(points);

		super.addComponent(top);
		super.initApp(window);
	}

}
