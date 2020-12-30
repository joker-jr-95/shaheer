package com.aait.shaheer.ui.activity.main.ui.inbox.chat;
        import android.content.Context;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.media.SoundPool;
        import android.os.Build;
        import android.provider.Settings;
        import android.util.Log;

        import com.aait.shaheer.R;

        import static android.content.Context.AUDIO_SERVICE;
        public class SoundPoolManager {

                private final Context context;
                private boolean playing = false;
        private boolean loaded = false;
        private boolean playingCalled = false;
        private float volume;
        private SoundPool soundPool;
        private int ringingSoundId;
        private int ringingStreamId;
        private int disconnectSoundId;
        private static SoundPoolManager instance;
                private MediaPlayer player;

                private SoundPoolManager(Context context) {
        // AudioManager audio settings for adjusting the volume
                this.context=context;
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_RING);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        volume = actualVolume / maxVolume;

        // Load the sounds
        int maxStreams = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        soundPool = new SoundPool.Builder()
        .setMaxStreams(maxStreams)
        .build();
        } else {
        soundPool = new SoundPool(maxStreams, AudioManager.STREAM_RING, 0);
        }
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
        loaded = true;
        if (playingCalled) {
        playRinging();
        playingCalled = false;
        }
        });
        ringingSoundId = soundPool.load(context, R.raw.incoming, 1);
        disconnectSoundId = soundPool.load(context, R.raw.disconnect, 1);
        }

        public static SoundPoolManager getInstance(Context context) {
        if (instance == null) {
        instance = new SoundPoolManager(context);
        }
        return instance;
        }

        public void playRinging() {
        if (loaded && !playing) {
        //ringingStreamId = soundPool.play(ringingSoundId, volume, volume, 1, -1, 1f);
                 player = MediaPlayer.create(context,
                        Settings.System.DEFAULT_RINGTONE_URI);

               player.start();
        playing = true;
        } else {
        playingCalled = true;
        }
        }
public void playRingingForPublisher() {
                        if (player!=null){
                                player.stop();
                                player.release();
                                player=null;
                                playing=false;
                                loaded=true;
                        }
        if (loaded && !playing) {
        ringingStreamId = soundPool.play(ringingSoundId, volume, volume, 1, -1, 1f);

        playing = true;
        } else {
        playingCalled = true;
        }
        //stop();
        }

        public void stopRinging() {
        if (playing) {
        //soundPool.stop(ringingStreamId);
        if (player!=null) {
                player.stop();
        }
        playing = false;
        }
        }

        public void stop() {
        if (playing) {
        //soundPool.stop(ringingStreamId);
        if (player!=null) {
                player.stop();
        }
        playing = false;
        }
        }

        public void playDisconnect() {
        if (loaded && !playing) {
                Log.e("stop_ring","1");
        soundPool.play(disconnectSoundId, volume, volume, 1, 0, 1f);
        playing = false;
        }
        }

        public void release() {
        if (soundPool != null) {
        soundPool.unload(ringingSoundId);
        soundPool.unload(disconnectSoundId);
        soundPool.release();
        if (player!=null) {
                player.release();
        }
        soundPool = null;
        }
        instance = null;
        }

        }
