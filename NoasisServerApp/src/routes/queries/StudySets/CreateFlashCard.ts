import { pool } from '../../../app'

export default async function createFlashCard(
  study_set_id: string,
  term: string,
  definition: string
) {

  const connection = await pool.getConnection()
  const result = await connection.execute(
    `INSERT INTO flashcards (study_set_id, term, definition) VALUES (?, ?, ?);`,
    [study_set_id, term, definition]
  )

  connection.release()
  return result
}
