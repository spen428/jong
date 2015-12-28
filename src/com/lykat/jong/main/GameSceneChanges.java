package com.lykat.jong.main;

class GameSceneChanges {

    boolean hand;
    boolean tsumoHai;
    boolean discards;
    boolean calls;
    boolean riichi;

    public GameSceneChanges() {
        super();
        setTrue();
    }

    private void setAll(boolean state) {
        this.hand = this.tsumoHai = this.discards = this.calls = this.riichi = state;
    }

    public void setTrue() {
        setAll(true);
    }

    public void setFalse() {
        setAll(false);
    }

}
