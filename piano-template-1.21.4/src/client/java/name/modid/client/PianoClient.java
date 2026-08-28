        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (OPEN_NOTE_BLOCKS.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new NoteBlockScreen());
                }
            }

            while (OPEN_SONG_EDITOR.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new SongScreen());
                }
            }

            while (PLAY_SONG.consumeClick()) {

                if (PLAYER.isPlaying()) {
                    PLAYER.stop();
                } else {
                    PLAYER.play(CONFIG.getCurrentSong());
                }
            }

            PLAYER.tick(client);
        });

        NoteBlockRenderer.register();
