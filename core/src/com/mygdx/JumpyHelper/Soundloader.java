package com.mygdx.JumpyHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by user on 13/4/2016.
 */
public class SoundLoader {

    public static Sound startSound;
    public static Sound thunder;
    public static Sound reverseSound;
    public static Sound weightSound;
    public static Sound jumpSound;
    public static Sound speedupSound;
    public static Sound flyingSound;

    public static void load() {
        startSound = Gdx.audio.newSound(Gdx.files.internal("Air_Horn.wav"));
        thunder = Gdx.audio.newSound(Gdx.files.internal("thunder.wav"));
        reverseSound = Gdx.audio.newSound(Gdx.files.internal("ufo.wav"));
        weightSound = Gdx.audio.newSound(Gdx.files.internal("Weight.wav"));
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump.wav"));
        speedupSound = Gdx.audio.newSound(Gdx.files.internal("speedup.wav"));
        flyingSound = Gdx.audio.newSound(Gdx.files.internal("beating-wings.wav"));
    }

}
