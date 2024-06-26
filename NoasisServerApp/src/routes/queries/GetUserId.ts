import { RowDataPacket } from 'mysql2'
import { pool } from '../../app'

interface IUserId extends RowDataPacket {
  id: string
}

export default async function getUserId(email: string) {
  const connection = await pool.getConnection()

  const [result] = await connection.execute<IUserId[]>(
    'SELECT id FROM users WHERE email=?;',
    [email]
  )

  if (result.length === 0) {
    throw new Error('This email does not exist.')
  }

  connection.release()
  return result[0].id
}
