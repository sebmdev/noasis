import { pool } from '../../../app'

export default async function deleteFlashCard(
  flashcard_id: string
) {
  const connection = await pool.getConnection()
  const [results] = await connection.execute(
    `DELETE FROM flashcards WHERE id = ?`,
    [flashcard_id]
  )

  connection.release()
  return results
}
