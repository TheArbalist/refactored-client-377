package com.runescape.scene;

import com.runescape.anim.Animation;
import com.runescape.anim.SpotAnimation;
import com.runescape.graphic.Model;
import com.runescape.renderable.Renderable;

public class AnimableObject extends Renderable {

	public int plane;
	public int x;
	public int y;
	public int z;
	public int loopCycle;
	public boolean transformCompleted = false;
	private SpotAnimation animation;
	private int eclapsedFrames;
	private int duration;

	public AnimableObject(int plane, int loopCycle, int loopCycleOffset, int animationIndex, int z, int y, int x) {
		this.animation = SpotAnimation.cache[animationIndex];
		this.plane = plane;
		this.x = x;
		this.y = y;
		this.z = z;
		this.loopCycle = loopCycle + loopCycleOffset;
		this.transformCompleted = false;
	}

	@Override
	public final Model getRotatedModel() {
		Model model = animation.getModel();
		if (model == null) {
			return null;
		}
		int frame = animation.sequences.frame2Ids[eclapsedFrames];
		Model animatedModel = new Model(true, Animation.exists(frame), false, model);
		if (!transformCompleted) {
			animatedModel.createBones();
			animatedModel.applyTransform(frame);
			animatedModel.triangleSkin = null;
			animatedModel.vectorSkin = null;
		}
		if (animation.resizeXY != 128 || animation.resizeZ != 128) {
			animatedModel.scaleT(animation.resizeXY, animation.resizeXY, animation.resizeZ);
		}
		if (animation.rotation != 0) {
			if (animation.rotation == 90) {
				animatedModel.rotate90Degrees(360);
			}
			if (animation.rotation == 180) {
				animatedModel.rotate90Degrees(360);
				animatedModel.rotate90Degrees(360);
			}
			if (animation.rotation == 270) {
				animatedModel.rotate90Degrees(360);
				animatedModel.rotate90Degrees(360);
				animatedModel.rotate90Degrees(360);
			}
		}
		animatedModel.applyLighting(64 + animation.modelBrightness, 850 + animation.modelShadow, -30, -50, -30, true);
		return animatedModel;
	}

	public final void nextFrame(int frame) {
		duration += frame;
		while (duration > animation.sequences.getFrameLength(eclapsedFrames)) {
			duration -= animation.sequences.getFrameLength(eclapsedFrames) + 1;
			eclapsedFrames++;
			if (eclapsedFrames >= animation.sequences.frameCount
					&& (eclapsedFrames < 0 || eclapsedFrames >= animation.sequences.frameCount)) {
				eclapsedFrames = 0;
				transformCompleted = true;
			}
		}
	}
}
