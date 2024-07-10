import { pool } from '../../../app'

export default async function deleteStudySet(study_set_id: string) {
  const connection = await pool.getConnection()
  try {
    // Start transaction
    await connection.beginTransaction()

    // Delete from flashcards
    await connection.execute(
      `DELETE FROM flashcards WHERE study_set_id = ?`,
      [study_set_id]
    )

    // Delete from study_set
    await connection.execute(
      `DELETE FROM study_sets WHERE id = ?`,
      [study_set_id]
    )

    // Commit the transaction
    await connection.commit()

    // Return success
    return { success: true }
  } catch (error) {
    // Rollback transaction in case of error
    await connection.rollback()
    return { error: error }
  } finally {
    // Release the connection back to the pool
    connection.release()
  }
}