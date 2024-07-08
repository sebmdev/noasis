import { pool } from '../../../app'

export default async function editFlashCard(
  flashcard_id: string,
  term: string,
  definition: string
) {
  
  console.log('search for', flashcard_id)
  const connection = await pool.getConnection()
  const [results] = await connection.execute(
    `UPDATE flashcards
    SET
        term = ?,
        definition = ?
    WHERE id = ?;`,
    [term, definition, flashcard_id]
  )

  connection.release()
  return results
}
