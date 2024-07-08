import { pool } from '../../../app'

export default async function listFlashCards(
  study_set_id: string
) {
  

  const connection = await pool.getConnection()
  const [rows] = await connection.execute(
    `SELECT id, term, definition FROM flashcards WHERE study_set_id = ?;`,
    [study_set_id]
  )

  connection.release()
  return rows
}
