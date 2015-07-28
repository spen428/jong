package com.lykat.jong.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public final class GraphicsConstants {

	/* Object Sizes */
	public static final float TILE_WIDTH_MM = 18.2f;
	public static final float TILE_HEIGHT_MM = 25.3f;
	public static final float TILE_THICKNESS_MM = 15.5f * 0.75f; // thin better?
	public static final float TILE_GAP_MM = 0.1f;

	public static final float RIICHI_WIDTH_MM = TILE_WIDTH_MM * 3.5f;
	public static final float RIICHI_THICKNESS_MM = TILE_THICKNESS_MM * 0.3f;
	public static final float RIICHI_HEIGHT_MM = RIICHI_THICKNESS_MM * 0.3f;

	public static final float PLAYING_SURFACE_RADIUS_MM = 350.0f;
	public static final float PLAYING_SURFACE_THICKNESS_MM = 1f;

	/* Object Positions */

	/**
	 * Distance of the camera from the playing surface in the Z plane.
	 */
	public static final float PLAYER_CAMERA_Z_OFFSET_MM = PLAYING_SURFACE_RADIUS_MM * 0.75f;
	public static final float PLAYER_CAMERA_Y_OFFSET_MM = PLAYING_SURFACE_RADIUS_MM * 1.2f;
	public static final float OVERHEAD_CAMERA_Z_OFFSET_MM = PLAYER_CAMERA_Z_OFFSET_MM * 2.1f;

	public static final float WALL_TILES_Y_OFFSET_MM = TILE_WIDTH_MM
			* GameConstants.WALL_WIDTH_TILES * 0.60f;
	public static final float HAND_TILES_Y_OFFSET_MM = (PLAYING_SURFACE_RADIUS_MM - WALL_TILES_Y_OFFSET_MM) * 1.70f;
	public static final float DISCARD_TILES_Y_OFFSET_MM = TILE_WIDTH_MM
			* GameConstants.DISCARD_WIDTH_TILES * 0.80f;
	public static final float RIICHI_STICK_Y_OFFSET_MM = RIICHI_WIDTH_MM * 0.65f;

	/* Static Models */
	private static final ModelBuilder mb = new ModelBuilder();
	private static final Material mat = new Material(
			ColorAttribute.createDiffuse(Color.WHITE));
	private static final int attr = Usage.Position | Usage.Normal
			| Usage.TextureCoordinates;

	public static final Model MODEL_TILE_OLD = mb.createBox(TILE_WIDTH_MM,
			TILE_HEIGHT_MM, TILE_THICKNESS_MM, mat, attr);

	public static final Model MODEL_RIICHI_STICK = mb.createBox(
			RIICHI_WIDTH_MM, RIICHI_THICKNESS_MM, RIICHI_HEIGHT_MM, mat, attr);

	/**
	 * Model of a Mahjong Tile, orientated face-down.
	 */
	public static final Model MODEL_TILE = buildTile();

	private static Model buildTile() {
		int attr = VertexAttributes.Usage.Position
				| VertexAttributes.Usage.Normal
				| VertexAttributes.Usage.TextureCoordinates;
		float halfTileWidth = TILE_WIDTH_MM / 2;
		float halfTileHeight = TILE_HEIGHT_MM / 2;
		float halfTileThick = TILE_THICKNESS_MM / 2;
		float faceThick = halfTileThick * 0.01f;
		float frontThick = TILE_THICKNESS_MM * 0.75f;
		float backThick = TILE_THICKNESS_MM * 0.25f;
		mb.begin();
		mb.part("back", GL20.GL_TRIANGLES, attr,
				new Material(ColorAttribute.createDiffuse(Color.ORANGE))).box(
				halfTileHeight, halfTileWidth,
				(faceThick / 2) + frontThick + (backThick / 2),
				halfTileHeight * 2, halfTileWidth * 2, backThick);
		mb.part("front", GL20.GL_TRIANGLES, attr,
				new Material(ColorAttribute.createDiffuse(Color.WHITE))).box(
				halfTileHeight, halfTileWidth,
				(faceThick / 2) + (frontThick / 2), halfTileHeight * 2,
				halfTileWidth * 2, frontThick);
		mb.part("face", GL20.GL_TRIANGLES, attr,
				new Material(ColorAttribute.createDiffuse(Color.WHITE))).box(
				halfTileHeight, halfTileWidth, 0, halfTileHeight * 2,
				halfTileWidth * 2, faceThick);
		return mb.end();
	}

	private GraphicsConstants() {
	}

}
