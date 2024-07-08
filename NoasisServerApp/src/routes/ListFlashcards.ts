import { Request, Response } from 'express'
import listFlashCards from './queries/StudySets/ListFlashCards';

export default async function ListFlashCards(req: Request, res: Response) {
  try {
    if (!req.session.user) {
      return res.sendStatus(401)
    }

    const studySetId = req.params.id;
    if (!studySetId) {
      return res.status(400).json({
        error: 'Study set not provided.',
      })
    }

    let flashcards = await listFlashCards(studySetId)
    console.log(flashcards)
    return res.status(200).json(flashcards)
    
  } catch (err: any) {
    console.error(err)
    return res.sendStatus(500)
  }
}
