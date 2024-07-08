import CryptoJS from 'crypto-js'
import { Request, Response } from 'express'
import createStudySet from './queries/StudySets/CreateStudySet'
import createFlashCard from './queries/StudySets/CreateFlashCard'
// import createPassword from '../util/queries/password/CreatePassword'

export default async function CreateFlashCard(req: Request, res: Response) {
  try {
    const studySetId = req.params.id;
    const term = req.body.term || ''
    const definition = req.body.definition || ''

    if (!req.session.user) {
      return res.sendStatus(401)
    }

    if (!studySetId) {
      return res.status(400).json({
        error: 'Study set not provided.',
      })
    }

    await createFlashCard(
      studySetId,
      term,
      definition
    )

    return res.status(200).json({
      message: 'Created new flashcard',
    })
    
  } catch (err: any) {
    console.error(err)
    return res.sendStatus(500)
  }
}
