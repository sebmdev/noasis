import { Request, Response } from 'express'

export default async function CheckSession(req: Request, res: Response) {
  console.log("user data", req.session.user)
  if (!req.session || !req.session.user) {
    return res.status(401).json({
      error: "Session expired."
    })
  }

  return res.sendStatus(200)

}
