import { Request, Response } from 'express'
import editFlashCard from './queries/StudySets/EditFlashCard';

export default async function EditFlashCard(req: Request, res: Response) {
  try {
    if (!req.session.user) {
      return res.sendStatus(401)
    }

    const term = req.body.term
    const definition = req.body.definition

    const flashcardId = req.params.id;
    if (!flashcardId) {
      return res.status(400).json({
        error: 'Flashcard not provided.',
      })
    }

    if (!term) {
      return res.status(400).json({
        error: 'Term not provided.',
      })
    }

    if (!definition) {
      return res.status(400).json({
        error: 'Definition not provided.',
      })
    }

    let result = await editFlashCard(flashcardId, term, definition)
    console.log(result)
    return res.sendStatus(200)
    
  } catch (err: any) {
    console.error(err)
    return res.sendStatus(500)
  }
}
