import { FieldPacket, RowDataPacket } from 'mysql2'
import { pool } from '../../../app'

interface FlashcardRecord extends RowDataPacket {
  id: string
  term: string
  definition: string
}

export default async function flashCardDetail(
  flashcard_id: string
) {
  
  console.log('search for', flashcard_id)
  const connection = await pool.getConnection()
  const [rows]: [FlashcardRecord[], FieldPacket[]] = await connection.execute(
    `SELECT id, term, definition FROM flashcards WHERE id = ?;`,
    [flashcard_id]
  )

  connection.release()
  return rows[0]
}
