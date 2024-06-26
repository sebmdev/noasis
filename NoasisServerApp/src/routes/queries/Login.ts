import bcrypt from 'bcrypt'
import { RowDataPacket } from 'mysql2'
import { pool } from '../../app'

interface IUserPassword extends RowDataPacket {
  password: string
}

export default async function login(email: string, password: string) {
  const connection = await pool.getConnection()

  const [result] = await connection.execute<IUserPassword[]>(
    'SELECT password FROM users WHERE email=?;',
    [email]
  )

  if (result.length === 0) {
    throw new Error('This email does not exist.')
  }

  const loginResult = await bcrypt.compare(password, result[0].password)
  connection.release()
  return loginResult
}
