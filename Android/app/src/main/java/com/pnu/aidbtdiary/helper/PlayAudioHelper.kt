package com.pnu.aidbtdiary.helper

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody

class PlayAudioHelper(private val context: Context) {

    @Volatile
    private var isPlaying = false
    @Volatile
    private var audioTrack: AudioTrack? = null

    suspend fun playStream(
        responseBody: ResponseBody,
        sampleRate: Int = 24000
    ) {
        withContext(Dispatchers.IO) {
            // 이전 재생이 남아있다면 완전히 정리
            stop()
            delay(100) // 시스템 자원 해제 대기

            isPlaying = true
            var inputStream: java.io.InputStream? = null
            var track: AudioTrack? = null

            try {
                inputStream = responseBody.byteStream()
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val buffer = ByteArray(minBufferSize)

                track = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize,
                    AudioTrack.MODE_STREAM
                )
                audioTrack = track
                track.play()

                var leftover: Byte? = null

                while (isPlaying) {
                    val read = inputStream.read(buffer)
                    if (read <= 0) break

                    var writeOffset = 0
                    var validBytes = read

                    // 이전 루프에서 남은 1바이트가 있다면, 첫 바이트와 합쳐서 write
                    if (leftover != null) {
                        // 새 버퍼의 첫 바이트와 leftover를 합쳐 2바이트 배열 생성
                        val twoBytes = byteArrayOf(leftover, buffer[0])
                        val written = track.write(twoBytes, 0, 2)
                        if (written < 0) break
                        writeOffset = 1
                        validBytes -= 1
                        leftover = null
                    }

                    // 남은 데이터가 홀수라면 마지막 1바이트는 남겨둠
                    if (validBytes % 2 != 0) {
                        leftover = buffer[writeOffset + validBytes - 1]
                        validBytes -= 1
                    }

                    if (validBytes > 0) {
                        val written = track.write(buffer, writeOffset, validBytes)
                        if (written < 0) break
                    }
                }
            } catch (e: Exception) {
                // 필요시 로그 처리
            } finally {
                try {
                    track?.stop()
                } catch (_: Exception) {}
                try {
                    track?.release()
                } catch (_: Exception) {}
                try {
                    inputStream?.close()
                } catch (_: Exception) {}
                isPlaying = false
                audioTrack = null
            }
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
