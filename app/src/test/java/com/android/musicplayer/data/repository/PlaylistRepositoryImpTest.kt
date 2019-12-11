package com.android.musicplayer.data.repository



class PlaylistRepositoryImpTest {


/*    @MockK
    lateinit var repository: PlaylistRepositoryImp


    @Before
    fun setUp() {
        MockKAnnotations.init(this)//for initialization
    }


    @Test
    fun testGetSongs() {
        val songs = mockk<List<Song>>()
        every { runBlocking { repository.getSongs() } } returns (songs)

        val result = repository.getSongs()
        MatcherAssert.assertThat(
            "Received result [$result] & mocked [$songs] must be matches on each other!",
            result,
            CoreMatchers.`is`(songs)
        )
    }


    @Test
    fun testSaveSongData() {
        val song = mockk<Song>()
        val id = 1L // id of stored song
        every {
            runBlocking {
                repository.saveSongData(song)
            }
        } returns (id)
        val result = repository.saveSongData(song)
        assertEquals(id, result)
    }


    @Test
    fun testDeleteSongFromDb() {
        val song = mockk<Song>()
        every{
            runBlocking {
                repository.delete(song)
            }
        }
    }*/

}