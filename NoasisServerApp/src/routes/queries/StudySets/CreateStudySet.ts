import { pool } from '../../../app'

export default async function createStudySet(
  user_id: string,
  title: string
) {

  const connection = await pool.getConnection()
  const result = await connection.execute(
    `INSERT INTO study_sets (created_by, title)
    VALUES(?,?)`,
    [user_id, title]
  )

  connection.release()
  return result
}
