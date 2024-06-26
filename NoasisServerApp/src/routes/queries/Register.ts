import { pool } from '../../app'

export default async function register(email: string, hashedPassword: string) {
  // Create new user

  const connection = await pool.getConnection()
  const result = await connection.execute(
    'INSERT INTO users (email, password) VALUES (?, ?)',
    [email, hashedPassword]
  )

  connection.release()
}
