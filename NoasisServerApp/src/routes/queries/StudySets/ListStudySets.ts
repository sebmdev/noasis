import { RowDataPacket } from 'mysql2'
import { pool } from '../../../app'

// interface StudySetRecord extends RowDataPacket {
//   id: string
//   title: string
// }

export default async function listStudySets(
  user_id: string
) {
  

  const connection = await pool.getConnection()
  const [rows] = await connection.execute(
    `SELECT id, title FROM study_sets WHERE created_by = ?;`,
    [user_id]
  )

  connection.release()
  return rows
}
