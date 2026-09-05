package com.afterscene.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [MediaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "afterscene_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                scope.launch(Dispatchers.IO) {
                    try {
                        INSTANCE?.mediaDao()?.let { dao ->
                            populateDatabase(dao)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            suspend fun populateDatabase(mediaDao: MediaDao) {
                val initialMedia = listOf(
                    MediaEntity(
                        title = "Your Name",
                        type = "Anime",
                        genre = "Romance, Fantasia",
                        description = "Uma história emocionante sobre amor, destino e conexão. Me fez refletir sobre escolhas e o tempo.",
                        emotion = "Nostalgia",
                        rating = 10,
                        imageUri = null
                    ),
                    MediaEntity(
                        title = "Attack on Titan",
                        type = "Anime",
                        genre = "Ação, Mistério, Fantasia",
                        description = "Uma obra-prima chocante com reviravoltas intensas do início ao fim. O desenvolvimento de personagens é incrível.",
                        emotion = "Impactante",
                        rating = 9,
                        imageUri = null
                    ),
                    MediaEntity(
                        title = "Death Note",
                        type = "Anime",
                        genre = "Suspense, Psicológico",
                        description = "A batalha de mentes mais icônica de todas. Muito tenso e inteligente, te prende a cada segundo.",
                        emotion = "Intenso",
                        rating = 9,
                        imageUri = null
                    ),
                    MediaEntity(
                        title = "Vincenzo",
                        type = "Dorama",
                        genre = "Comédia, Crime, Drama",
                        description = "Um advogado da máfia italiana faz justiça com as próprias mãos na Coreia. Muito divertido e repleto de ação.",
                        emotion = "Intenso",
                        rating = 8,
                        imageUri = null
                    ),
                    MediaEntity(
                        title = "Goblin",
                        type = "Dorama",
                        genre = "Fantasia, Romance, Drama",
                        description = "Uma das trilhas sonoras e visuais mais bonitos. Uma história de amor trágica e reconfortante ao mesmo tempo.",
                        emotion = "Nostalgia",
                        rating = 9,
                        imageUri = null
                    ),
                    MediaEntity(
                        title = "Twenty Five Twenty One",
                        type = "Dorama",
                        genre = "Romance, Drama, Esporte",
                        description = "Mostra de forma muito realista as dores do crescimento, os sonhos da juventude e a força do primeiro amor.",
                        emotion = "Triste",
                        rating = 10,
                        imageUri = null
                    ),
                    MediaEntity(
                        title = "Interest of Love",
                        type = "Dorama",
                        genre = "Romance, Drama",
                        description = "Um romance lento e frustrante sobre as diferenças de classes sociais e a indecisão no amor. Relação complexa.",
                        emotion = "Triste",
                        rating = 7,
                        imageUri = null
                    ),
                    MediaEntity(
                        title = "A Viagem de Chihiro",
                        type = "Filme",
                        genre = "Animação, Fantasia, Aventura",
                        description = "Um filme lindo e mágico sobre crescimento, coragem e identidade. Traz uma sensação profunda de paz.",
                        emotion = "Conforto",
                        rating = 10,
                        imageUri = null
                    )
                )
                for (media in initialMedia) {
                    mediaDao.insert(media)
                }
            }
        }
    }
}
