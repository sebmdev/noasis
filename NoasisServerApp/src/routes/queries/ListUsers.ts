import { pool } from '../../app'

export default async function listUsers(
  query: string
) {
  
  let q = `%${query}%`

  const connection = await pool.getConnection()
  const [rows] = await connection.execute(
    `SELECT id, email FROM users WHERE email LIKE ?;`,
    [q]
  )

  connection.release()
  return rows
}
