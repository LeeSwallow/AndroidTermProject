package com.pnu.aidbtdiary.network

import com.pnu.aidbtdiary.dto.DbtDiaryRemote
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class SupaClient(
    url: String,
    key: String
) {
    companion object {
        private const val CREATE_SCHEMA = """
            CREATE TABLE IF NOT EXISTS dbt_diary (
                id SERIAL PRIMARY KEY,
                date DATE NOT NULL,
                situation TEXT NOT NULL,
                emotion TEXT NOT NULL,
                intensity INTEGER NOT NULL,
                thought TEXT NOT NULL,
                behavior TEXT NOT NULL,
                dbt_skill TEXT NOT NULL,
                deleted BOOLEAN DEFAULT FALSE,
                sentiment BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """
    }

    private val client: SupabaseClient = createSupabaseClient(url, key) { install(Postgrest) }

    suspend fun ensureDbtDiarySchema(): Boolean {
        return try {
            client.postgrest.from("dbt_diary").select().decodeSingle<DbtDiaryRemote>()
            true
        } catch (e: Exception) {
            try {
                client.postgrest.rpc(
                    "execute_sql",
                    buildJsonObject { put("sql", JsonPrimitive(CREATE_SCHEMA)) }
                )
                true
            } catch (ex: Exception) {
                false
            }
        }
    }
}