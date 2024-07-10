import { pool } from '../../../app'

export default async function editStudySet(
  study_set_id: string,
  title: string
) {
  
  const connection = await pool.getConnection()
  const [results] = await connection.execute(
    `UPDATE study_sets
    SET
      title = ?
    WHERE id = ?;`,
    [title, study_set_id]
  )

  connection.release()
  return results
}