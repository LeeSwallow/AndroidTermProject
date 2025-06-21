package com.pnu.aidbtdiary.helper

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import okhttp3.ResponseBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlayAudioHelper(private val context: Context) {

    @Volatile
    private var isPlaying = false
    @Volatile
    private var audioTrack: AudioTrack? = null

    suspend fun playStream(responseBody: ResponseBody, sampleRate: Int = 24000) {
        withContext(Dispatchers.IO) {
            if (isPlaying) {
                stop()
                return@withContext
            }
            isPlaying = true
            val inputStream = responseBody.byteStream()
            inputStream.skip(44)
            val buffer = ByteArray(2048)
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val track = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize,
                AudioTrack.MODE_STREAM
            )
            audioTrack = track
            track.play()
            while (isPlaying) {
                val read = inputStream.read(buffer)
                if (read <= 0) break
                track.write(buffer, 0, read)
            }
            track.stop()
            track.release()
            inputStream.close()
            isPlaying = false
            audioTrack = null
        }
    }

    fun stop() {
        isPlaying = false
        audioTrack?.let {
            try {
                it.stop()
                it.release()
            } catch (_: Exception) {}
        }
        audioTrack = null
    }

    fun isPlaying(): Boolean = isPlaying
}

