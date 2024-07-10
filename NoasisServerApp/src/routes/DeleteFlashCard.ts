import { Request, Response } from 'express'
import deleteFlashCard from './queries/StudySets/DeleteFlashCard';

export default async function DeleteFlashCard(req: Request, res: Response) {
  try {
    if (!req.session.user) {
      return res.sendStatus(401)
    }

    const flashcardId = req.params.id;
    if (!flashcardId) {
      return res.status(400).json({
        error: 'Flashcard not provided.',
      })
    }

    let flashcard = await deleteFlashCard(flashcardId)
    console.log(flashcard)
    return res.sendStatus(200)

  } catch (err: any) {
    console.error(err)
    return res.sendStatus(500)
  }
}
