package com.example.mipluvimetro.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.fragment.app.Fragment
import com.example.mipluvimetro.R

class AyudaFragment : Fragment(){
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ayuda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerView = view.findViewById(R.id.playerView)
    }

    // Iniciamos el vídeo cuando la pantalla se hace visible
    override fun onStart() {
        super.onStart()
        inicializarReproductor()
    }

    // Detenemos el vídeo si el usuario cambia de pestaña (para ahorrar batería y memoria)
    override fun onStop() {
        super.onStop()
        liberarReproductor()
    }

    private fun inicializarReproductor() {
        player = ExoPlayer.Builder(requireContext()).build()

        playerView.player = player

        val rawUri = Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.video_tutorial)
        val mediaItem = MediaItem.fromUri(rawUri)

        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = false // false = El usuario debe dar al Play; true = Autoplay
    }

    private fun liberarReproductor() {
        player?.release()
        player = null
    }
}