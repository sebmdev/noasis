import { pool } from '../../app'

export default async function createPassword(
  user_id: string,
  url: string,
  username: string,
  password: string,
  description: string
) {
  // Create new user

  const connection = await pool.getConnection()
  const result = await connection.execute(
    `INSERT INTO passwords (user_id, url, website, username, password, description) VALUES (
            ?,
            ?,
            ?,
            ?,
            ?,
            ?
        );`,
    [user_id, url, username, password, description]
  )

  connection.release()
  return result
}
