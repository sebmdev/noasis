import { Request, Response } from 'express'
import flashCardDetail from './queries/StudySets/FlashCardDetail';

export default async function FlashcardDetail(req: Request, res: Response) {
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

    let flashcard = await flashCardDetail(flashcardId)
    console.log(flashcard)
    return res.status(200).json(flashcard)
    
  } catch (err: any) {
    console.error(err)
    return res.sendStatus(500)
  }
}
