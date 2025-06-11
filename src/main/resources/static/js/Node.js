app.post('/feedback', async (req, res) => {
  const { usuarioId, convocatoriaId, etiquetaId, aprobado } = req.body;

  try {
    const [existingFeedback] = await db.query(`
      SELECT 1 FROM FeedBack 
      WHERE usuarioId = ? AND convocatoriaId = ? AND etiquetaId = ?
    `, [usuarioId, convocatoriaId, etiquetaId]);

    if (existingFeedback.length > 0) {
      return res.status(400).json({ error: 'Ya has evaluado este etiquetado.' });
    }

    await db.query(`
      INSERT INTO FeedBack (usuarioId, convocatoriaId, etiquetaId, aprobado, fecha, createdAt, updatedAt)
      VALUES (?, ?, ?, ?, NOW(), NOW(), NOW())
    `, [usuarioId, convocatoriaId, etiquetaId, aprobado]);

    // El trigger actualizará ETIQUETADO.valoracion automáticamente

    res.status(200).json({ success: true });
  } catch (err) {
    console.error('Error en el insert de feedback:', err);
    res.status(500).json({ error: 'Error al registrar el feedback' });
  }
});
